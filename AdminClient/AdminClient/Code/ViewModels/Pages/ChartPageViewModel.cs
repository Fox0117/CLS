using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Input;
using AdminClient.Code.Utils;
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

        public ObservableCollection<ChartItemModel> ChartItems { get; } = new ObservableCollection<ChartItemModel>();

        public PropertyProvider<int> VerticalAxisMaximum { get; }

        public ICommand ProcessRangeCommand => _processRangeCommand;
        private readonly ActionCommand _processRangeCommand;

        public ChartPageViewModel()
        {
            VerticalAxisMaximum = CreateProviderWithNotify<int>(nameof(VerticalAxisMaximum));

            _processRangeCommand = new ActionCommand(ProcessRangeCommand_Execute, () => !IsBusy);

            PropertyChanged += OnPropertyChanged;
        }

        private async void ProcessRangeCommand_Execute()
        {
            using (BusyDisposable())
            {
                await Task.Delay(1000);

                InitExamples();
            }
        }

        public override async Task LoadItems()
        {
            using (BusyDisposable())
            {
                await Task.Delay(1000);

                InitExamples();
            }
        }

        private void InitExamples(int count = 30)
        {
            ChartItems.Clear();

            var random = new Random();

            var list = new List<(DateTime date, int count)>();

            int previous = 50;

            for (int i = 0; i < count; i++)
            {
                int current = Math.Max(30, previous + random.Next(-20, 20));
                list.Add((DateTime.Now.AddSeconds(i), current));
                previous = current;
            }

            VerticalAxisMaximum.Value = list.Max(it => it.count) + 10;

            list.Select(it => new ChartItemModel(it.date, it.count)).ForEach(ChartItems.Add);
        }

        private void RaiseCommandsCanExecute()
        {
            _processRangeCommand.RaiseCanExecuteChanged();
        }

        private void OnPropertyChanged(object sender, PropertyChangedEventArgs args)
        {
            switch (args.PropertyName)
            {
                case nameof(IsBusy):
                    RaiseCommandsCanExecute();
                    break;
            }
        }
    }
}
