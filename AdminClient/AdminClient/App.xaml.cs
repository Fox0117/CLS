using System.Windows;
using AdminClient.Windows;
using Bugsnag.Clients;

namespace AdminClient
{
    public partial class App
    {
        protected override void OnStartup(StartupEventArgs e)
        {
            WPFClient.Start();

            new AdminWindow().Show();
        }
    }
}
