using System.Windows;
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

        private async void ChartPage_OnLoaded(object sender, RoutedEventArgs e)
        {
            await ViewModel.LoadItems();

            //await ViewModel.LoadItems();

            //Chart.Series[0].ref
        }
    }
}
