using System.ComponentModel;
using System.Net;
using System.Threading.Tasks;
using System.Windows;
using AdminClient.Code.Interfaces;
using AdminClient.Code.Models;
using AdminClient.Code.Utils;
using AdminClient.Resources.JavaScriptExamples;
using AdminClient.Resources.Localizations;
using Bugsnag;
using Bugsnag.Clients;
using ICSharpCode.AvalonEdit;
using MVVM_Tools.Code.Commands;

using MessageBox = System.Windows.MessageBox;

namespace AdminClient.Code.ViewModels.Pages
{
    internal class ScriptPageViewModel : ViewModelBase
    {
        private readonly TextEditor _editor;
        private readonly IDatabaseModel _databaseModel = new DatabaseModel();

        public IActionCommand TestScriptCommand { get; }
        public IActionCommand SendScriptCommand { get; }

        public ScriptPageViewModel(TextEditor editor)
        {
            _editor = editor;

            TestScriptCommand = new ActionCommand(TestScriptCommand_Execute, () => !IsBusy);
            SendScriptCommand = new ActionCommand(SendScriptCommand_Execute, () => !IsBusy);

            PropertyChanged += OnPropertyChanged;
        }

        private void TestScriptCommand_Execute()
        {
            const int totalVisits = 101;
            const int lastYearVisits = 50;
            const int lastMonthVisits = 30;
            const int lastWeekVisits = 15;
            const int lastDayVisits = 2;

            using (BusyDisposable())
            {
                var browser = new System.Windows.Forms.WebBrowser {DocumentText = "0"};
                browser.Document.OpenNew(true);
                string docString = $@"
                <html>
                    <head>
                        <script type=""text/javascript"">
                            function getMessageInternal() {{
                                return getMessage(
                                    {{
                                        ""total_visits"": {totalVisits},
                                        ""last_year_visits"": {lastYearVisits},
                                        ""last_month_visits"": {lastMonthVisits},
                                        ""last_week_visits"": {lastWeekVisits},
                                        ""last_day_visits"": {lastDayVisits}
                                    }}
                                );
                            }}

                            {_editor.Text}
                        </script>
                    </head>
                </html>";
                browser.Document.Write(docString);
                browser.Refresh();

                var result = browser.Document.InvokeScript("getMessageInternal", null);

                if (result == null)
                {
                    MessageBox.Show(
                        StringResources.ScriptError_Content, StringResources.Error_Title,
                        MessageBoxButton.OK, MessageBoxImage.Exclamation
                    );
                }
                else
                {
                    MessageBox.Show(
                        result.ToString(), StringResources.Information_Title,
                        MessageBoxButton.OK, MessageBoxImage.Information
                    );
                }

                browser.Dispose();
            }
        }

        private async void SendScriptCommand_Execute()
        {
            using (BusyDisposable())
            {
                try
                {
                    await _databaseModel.SendScriptAsync(_editor.Text);
                }
                catch (WebException ex)
                {
                    WPFClient.NotifyAsync(new WebException("Can't send script", ex), Severity.Warning);
                    MessageUtils.ShowExclamation(StringResources.ErrorWhileConnecting_Content);
                }
            }
        }

        private void OnPropertyChanged(object sender, PropertyChangedEventArgs args)
        {
            switch (args.PropertyName)
            {
                case nameof(IsBusy):
                    RaiseCommandsCanExecute();
                    break;
            }
        }

        private void RaiseCommandsCanExecute()
        {
            TestScriptCommand.RaiseCanExecuteChanged();
            SendScriptCommand.RaiseCanExecuteChanged();
        }

        public override async Task LoadItems()
        {
            using (BusyDisposable())
            {
                try
                {
                    var response = await _databaseModel.GetScriptAsync();

                    if (!string.IsNullOrEmpty(response.script))
                    {
                        _editor.Text = response.script;
                    }
                    else
                    {
                        if (
                            MessageBox.Show(
                                StringResources.ScriptNotExists_Content,
                                StringResources.Information_Title,
                                MessageBoxButton.YesNo) ==
                            MessageBoxResult.Yes
                        )
                        {
                            _editor.Text = JSExampleResources.Example_2;
                        }
                    }
                }
                catch (WebException ex)
                {
                    WPFClient.NotifyAsync(new WebException("Can't load script", ex), Severity.Warning);
                    MessageUtils.ShowExclamation(StringResources.ErrorWhileConnecting_Content);
                }
            }
        }
    }
}
