using System.ComponentModel;
using System.Threading.Tasks;
using System.Windows;
using MVVM_Tools.Code.Classes;
using MVVM_Tools.Code.Disposables;

namespace AdminClient.Code.ViewModels
{
    internal class ViewModelBase : BindableBase
    {
        private static readonly DependencyObject DesignerObject = new DependencyObject();

        public bool IsBusy
        {
            get => _isBusy;
            protected set => SetProperty(ref _isBusy, value);
        }
        private bool _isBusy;

        protected bool IsInDesigner()
        {
            return DesignerProperties.GetIsInDesignMode(DesignerObject);
        }

        protected CustomBoolDisposable BusyDisposable()
        {
            return new CustomBoolDisposable(val => IsBusy = val);
        }

        public virtual Task LoadItems() => Task.CompletedTask;
    }
}
