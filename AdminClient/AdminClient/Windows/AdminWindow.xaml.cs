using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using AdminClient.Code.ViewModels.Windows;

namespace AdminClient.Windows
{
    public partial class AdminWindow
    {
        public AdminWindow()
        {
            InitializeComponent();

            ViewModel = new AdminWindowViewModel();

            Closed += (sender, args) => Application.Current.Shutdown(0);
        }

        internal AdminWindowViewModel ViewModel
        {
            get => DataContext as AdminWindowViewModel;
            private set => DataContext = value;
        }

        private void Frame_OnNavigated(object sender, NavigationEventArgs e)
        {
            (sender as Frame).NavigationService.RemoveBackEntry();
        }
    }
}
