using System.ComponentModel;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using AdminClient.Resources.JavaScriptExamples;
using ICSharpCode.AvalonEdit;
using MVVM_Tools.Code.Commands;

namespace AdminClient.Code.ViewModels.Pages
{
    internal class ScriptPageViewModel : ViewModelBase
    {
        private readonly TextEditor _editor;

        public ICommand SendScriptCommand => _sendScriptCommand;
        private readonly ActionCommand _sendScriptCommand;

        public ScriptPageViewModel(TextEditor editor)
        {
            _editor = editor;

            _sendScriptCommand = new ActionCommand(SendScriptCommand_Execute, () => !IsBusy);

            PropertyChanged += OnPropertyChanged;
        }

        private async void SendScriptCommand_Execute()
        {
            using (BusyDisposable())
            {
                await Task.Delay(1000);
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
            _sendScriptCommand.RaiseCanExecuteChanged();
        }

        public override async Task LoadItems()
        {
            using (BusyDisposable())
            {
                await Task.Delay(1000);

                using (var stream = new MemoryStream(Encoding.UTF8.GetBytes(JSExampleResources.Example_1)))
                    _editor.Load(stream);
            }
        }
    }
}
