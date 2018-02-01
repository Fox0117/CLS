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
    }
}
