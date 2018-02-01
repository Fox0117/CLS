using System;
using System.Collections.ObjectModel;
using System.Windows.Input;
using AdminClient.Code.Utils;
using AdminClient.Code.ViewModels.Controls;
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

        public ICommand LeftMenuClickCommand => _leftMenuClickCommand;
        private readonly ActionCommand<LeftMenuItemViewModel> _leftMenuClickCommand;

        public AdminWindowViewModel()
        {
            _leftMenuClickCommand = new ActionCommand<LeftMenuItemViewModel>(LeftCommandClickCommand_Execute, _ => !IsBusy);

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
                    Title = "Chart",
                    IsSelected = true,
                    PageUri = new Uri("../Pages/ChartPage.xaml", UriKind.Relative)
                }
            );

            _leftMenuItems.Add(
                new LeftMenuItemViewModel
                {
                    Title = "Script",
                    PageUri = new Uri("../Pages/ScriptPage.xaml", UriKind.Relative)
                }
            );
        }
    }
}
