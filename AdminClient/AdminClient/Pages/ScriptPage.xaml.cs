using System.Windows;
using AdminClient.Code.ViewModels.Pages;

namespace AdminClient.Pages
{
    public partial class ScriptPage
    {
        public ScriptPage()
        {
            InitializeComponent();

            ViewModel = new ScriptPageViewModel(Editor);
        }

        internal ScriptPageViewModel ViewModel
        {
            get => DataContext as ScriptPageViewModel;
            private set => DataContext = value;
        }

        private async void ScriptPage_OnLoaded(object sender, RoutedEventArgs e)
        {
            await ViewModel.LoadItems();
        }
    }
}
