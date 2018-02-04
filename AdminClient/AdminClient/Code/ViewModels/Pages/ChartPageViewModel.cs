using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Net;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;
using AdminClient.Code.Interfaces;
using AdminClient.Code.Models;
using AdminClient.Code.Responses;
using AdminClient.Code.Utils;
using AdminClient.Resources.Localizations;
using MVVM_Tools.Code.Commands;
using MVVM_Tools.Code.Providers;

namespace AdminClient.Code.ViewModels.Pages
{
    internal class ChartPageViewModel : ViewModelBase
    {
        internal class ChartItemModel
        {
            public int Count { get; }
            public DateTime Date { get; }

            public ChartItemModel(DateTime date, int count)
            {
                Count = count;
                Date = date;
            }
        }

        private delegate List<(DateTime date, int count)> GroupByDelegate(IList<EntriesResponse.OneEntry> entries);

        private readonly IDatabaseModel _databaseModel = new DatabaseModel();

        private readonly Dictionary<int, GroupByDelegate> _groupOptions = new Dictionary<int, GroupByDelegate>
        {
            { 0, EntriesUtils.GroupByMonth },
            { 1, EntriesUtils.GroupByDay },
            { 2, EntriesUtils.GroupByHour },
            { 3, EntriesUtils.GroupByMinute }
        };

        private List<EntriesResponse.OneEntry> _entries;

        public ObservableCollection<ChartItemModel> ChartItems { get; } = new ObservableCollection<ChartItemModel>();
        public ObservableCollection<string> GroupingByItems { get; } = new ObservableCollection<string>();
        public PropertyProvider<int> GroupingByIndex { get; }

        public PropertyProvider<DateTime> MinimumDate { get; }
        public PropertyProvider<DateTime> MaximumDate { get; }

        public ICommand ProcessRangeCommand => _processRangeCommand;
        private readonly ActionCommand _processRangeCommand;

        public ChartPageViewModel()
        {
            MinimumDate = CreateProviderWithNotify<DateTime>(nameof(MinimumDate));
            MaximumDate = CreateProviderWithNotify<DateTime>(nameof(MaximumDate));
            GroupingByIndex = CreateProviderWithNotify<int>(nameof(GroupingByIndex));

            _processRangeCommand = new ActionCommand(ProcessRangeCommand_Execute, () => !IsBusy);

            GroupingByItems.AddAll(
                StringResources.GroupByMonth_Item,
                StringResources.GroupByDay_Item,
                StringResources.GroupByHour_Item,
                StringResources.GroupByMinute_Item
            );

            GroupingByIndex.Value = 2;

            PropertyChanged += OnPropertyChanged;
        }

        public override async Task LoadItems()
        {
            using (BusyDisposable())
            {
                try
                {
                    await LoadDates();
                }
                catch (WebException)
                {
                    ShowExclamation(StringResources.ErrorWhileConnecting_Content);
                }
            }
        }

        private async void ProcessRangeCommand_Execute()
        {
            using (BusyDisposable())
            {
                try
                {
                    await LoadEntries();
                    await VisualizeEntries();
                }
                catch (WebException)
                {
                    ShowExclamation(StringResources.ErrorWhileConnecting_Content);
                }
            }
        }

        private async Task LoadDates()
        {
            var dates = await _databaseModel.GetEntriesRangeAsync();

            MinimumDate.Value = dates.minimum_date;
            MaximumDate.Value = dates.maximum_date;
        }

        private async Task LoadEntries()
        {
            var items = await _databaseModel.GetEntriesAsync(MinimumDate.Value, MaximumDate.Value);

            _entries = items.entries;

            // todo: remove when server side will be done
            _entries = await Task.Run(() => EntriesUtils.GenEntries(30000));
            // ---

            _entries.Sort((f, s) => f.date.CompareTo(s.date));
        }

        private async Task VisualizeEntries()
        {
            ChartItems.Clear();

            var groupFunc = _groupOptions[GroupingByIndex.Value];

            List<(DateTime date, int count)> entries = await Task.Run(() => groupFunc(_entries));

            entries.ForEach(it => ChartItems.Add(new ChartItemModel(it.date, it.count)));
        }

        private static void ShowExclamation(string message)
        {
            MessageBox.Show(
                message, StringResources.Error_Title,
                MessageBoxButton.OK, MessageBoxImage.Exclamation
            );
        }

        private void RaiseCommandsCanExecute()
        {
            _processRangeCommand.RaiseCanExecuteChanged();
        }

        private async void OnPropertyChanged(object sender, PropertyChangedEventArgs args)
        {
            switch (args.PropertyName)
            {
                case nameof(IsBusy):
                    RaiseCommandsCanExecute();
                    break;
                case nameof(GroupingByIndex):
                    if (_entries != null)
                        await VisualizeEntries();
                    break;
            }
        }
    }
}
