using System.Windows.Controls;
using System.Windows.Navigation;
using AdminClient.Code.ViewModels.Windows;

namespace AdminClient.Windows
{
    public partial class MainWindow
    {
        public MainWindow()
        {
            InitializeComponent();

            ViewModel = new AdminWindowViewModel();
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
