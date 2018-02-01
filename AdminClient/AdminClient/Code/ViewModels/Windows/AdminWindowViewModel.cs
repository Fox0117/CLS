using System;
using System.Windows.Input;
using MVVM_Tools.Code.Commands;

namespace AdminClient.Code.ViewModels.Windows
{
    internal class AdminWindowViewModel : ViewModelBase
    {
        public Uri CurrentPageUri
        {
            get => _currentPageUri;
            private set => SetProperty(ref _currentPageUri, value);
        }
        private Uri _currentPageUri = new Uri("../Pages/ChartPage.xaml", UriKind.Relative);

        public ICommand LeftMenuClickCommand => _leftMenuClickCommand;
        private readonly ActionCommand<Uri> _leftMenuClickCommand;

        public AdminWindowViewModel()
        {
            _leftMenuClickCommand = new ActionCommand<Uri>(LeftCommandClickCommand_Execute, _ => !IsBusy);

            InitLeftMenu();
        }

        private void LeftCommandClickCommand_Execute(Uri parameter)
        {
            CurrentPageUri = parameter;
        }

        private void InitLeftMenu()
        {
        }
    }
}
