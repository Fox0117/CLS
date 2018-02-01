using System.Windows;

namespace AdminClient.Controls
{
    public partial class BusyControl
    {
        public static readonly DependencyProperty IsBusyProperty = DependencyProperty.Register(
            "IsBusy", typeof(bool), typeof(BusyControl), new PropertyMetadata(default(bool)));

        public bool IsBusy
        {
            get => (bool) GetValue(IsBusyProperty);
            set => SetValue(IsBusyProperty, value);
        }

        public BusyControl()
        {
            InitializeComponent();
        }
    }
}
