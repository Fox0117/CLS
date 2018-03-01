using System;
using System.Collections.ObjectModel;
using AdminClient.Code.Utils;
using AdminClient.Code.ViewModels.Controls;
using AdminClient.Resources.Localizations;
using MVVM_Tools.Code.Commands;

namespace AdminClient.Code.ViewModels.Windows
{
    internal class AdminWindowViewModel : ViewModelBase
    {
        public LeftMenuItemViewModel SelectedPage
        {
            get => _selectedPage;
            private set => SetProperty(ref _selectedPage, value);
        }
        private LeftMenuItemViewModel _selectedPage;

        public ReadOnlyObservableCollection<LeftMenuItemViewModel> LeftMenuItems { get; }
        private readonly ObservableCollection<LeftMenuItemViewModel> _leftMenuItems;

        public IActionCommand<LeftMenuItemViewModel> LeftMenuClickCommand { get; }

        public AdminWindowViewModel()
        {
            LeftMenuClickCommand = 
                new ActionCommand<LeftMenuItemViewModel>(LeftCommandClickCommand_Execute, _ => !IsBusy);

            _leftMenuItems = new ObservableCollection<LeftMenuItemViewModel>();
            LeftMenuItems = new ReadOnlyObservableCollection<LeftMenuItemViewModel>(_leftMenuItems);

            InitLeftMenu();

            SetSelectedItem(LeftMenuItems[0]);
        }

        private void SetSelectedItem(LeftMenuItemViewModel item)
        {
            LeftMenuItems.ForEach(it => it.IsSelected = false);
            SelectedPage = item;
            item.IsSelected = true;
        }

        private void LeftCommandClickCommand_Execute(LeftMenuItemViewModel item)
        {
            SetSelectedItem(item);
        }

        private void InitLeftMenu()
        {
            _leftMenuItems.Add(
                new LeftMenuItemViewModel
                {
                    Title = StringResources.ChartMenuItem_Title,
                    IsSelected = true,
                    PageUri = new Uri("../Pages/ChartPage.xaml", UriKind.Relative)
                }
            );

            _leftMenuItems.Add(
                new LeftMenuItemViewModel
                {
                    Title = StringResources.ScriptMenuItem_Title,
                    PageUri = new Uri("../Pages/ScriptPage.xaml", UriKind.Relative)
                }
            );
        }
    }
}
