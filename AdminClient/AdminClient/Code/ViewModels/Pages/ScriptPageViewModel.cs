﻿using System.ComponentModel;
using System.IO;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;
using AdminClient.Code.Interfaces;
using AdminClient.Code.Models;
using AdminClient.Code.Utils;
using AdminClient.Resources.JavaScriptExamples;
using AdminClient.Resources.Localizations;
using ICSharpCode.AvalonEdit;
using MVVM_Tools.Code.Commands;

using MessageBox = System.Windows.MessageBox;

namespace AdminClient.Code.ViewModels.Pages
{
    internal class ScriptPageViewModel : ViewModelBase
    {
        private readonly TextEditor _editor;
        private readonly IDatabaseModel _databaseModel = new DatabaseModel();

        public ICommand TestScriptCommand => _testScriptCommand;
        private readonly ActionCommand _testScriptCommand;

        public ICommand SendScriptCommand => _sendScriptCommand;
        private readonly ActionCommand _sendScriptCommand;

        public ScriptPageViewModel(TextEditor editor)
        {
            _editor = editor;

            _testScriptCommand = new ActionCommand(TestScriptCommand_Execute, NotBusy);
            _sendScriptCommand = new ActionCommand(SendScriptCommand_Execute, NotBusy);

            PropertyChanged += OnPropertyChanged;
        }

        private void TestScriptCommand_Execute()
        {
            using (BusyDisposable())
            {
                var browser = new System.Windows.Forms.WebBrowser {DocumentText = "0"};
                browser.Document.OpenNew(true);
                browser.Document.Write(
                    $@"
                <html>
                    <head>
                        <script type=""text/javascript"">
                            {_editor.Text}
                        </script>
                    </head>
                </html>"
                );
                browser.Refresh();

                var result = browser.Document.InvokeScript("getAnswer");

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
                    MessageUtils.ShowExclamation(StringResources.ErrorWhileConnecting_Content + "\n" + ex);
                }
            }
        }

        private bool NotBusy() => !IsBusy;

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
            _testScriptCommand.RaiseCanExecuteChanged();
            _sendScriptCommand.RaiseCanExecuteChanged();
        }

        public override async Task LoadItems()
        {
            using (BusyDisposable())
            {
                try
                {
                    var response = await _databaseModel.GetScriptAsync();

                    await Task.Delay(1000);

                    using (var stream = new MemoryStream(Encoding.UTF8.GetBytes(JSExampleResources.Example_2)))
                        _editor.Load(stream);
                }
                catch (WebException ex)
                {
                    MessageUtils.ShowExclamation(StringResources.ErrorWhileConnecting_Content + "\n" + ex);
                }
            }
        }
    }
}
