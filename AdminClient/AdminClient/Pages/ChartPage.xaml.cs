using AdminClient.Code.ViewModels.Pages;

namespace AdminClient.Pages
{
    public partial class ChartPage
    {
        public ChartPage()
        {
            InitializeComponent();

            ViewModel = new ChartPageViewModel();
        }

        internal ChartPageViewModel ViewModel
        {
            get => DataContext as ChartPageViewModel;
            set => DataContext = value;
        }
    }
}
