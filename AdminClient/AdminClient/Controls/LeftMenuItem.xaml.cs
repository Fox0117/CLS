using System.Windows;
using System.Windows.Input;
using System.Windows.Media;

namespace AdminClient.Controls
{
    public partial class LeftMenuItem
    {
        public static readonly DependencyProperty TitleProperty = DependencyProperty.Register(
            "Title", typeof(string), typeof(LeftMenuItem), new PropertyMetadata(default(string)));

        public string Title
        {
            get => (string) GetValue(TitleProperty);
            set => SetValue(TitleProperty, value);
        }

        public static readonly DependencyProperty HoverBrushProperty = DependencyProperty.Register(
            "HoverBrush", typeof(Brush), typeof(LeftMenuItem), new PropertyMetadata(default(Brush)));

        public Brush HoverBrush
        {
            get => (Brush) GetValue(HoverBrushProperty);
            set => SetValue(HoverBrushProperty, value);
        }

        public static readonly DependencyProperty ClickCommandProperty = DependencyProperty.Register(
            "ClickCommand", typeof(ICommand), typeof(LeftMenuItem), new PropertyMetadata(default(ICommand)));

        public ICommand ClickCommand
        {
            get => (ICommand) GetValue(ClickCommandProperty);
            set => SetValue(ClickCommandProperty, value);
        }

        public static readonly DependencyProperty ClickCommandParameterProperty = DependencyProperty.Register(
            "ClickCommandParameter", typeof(object), typeof(LeftMenuItem), new PropertyMetadata(default));

        public object ClickCommandParameter
        {
            get => GetValue(ClickCommandParameterProperty);
            set => SetValue(ClickCommandParameterProperty, value);
        }

        public static readonly DependencyProperty IsSelectedProperty = DependencyProperty.Register(
            "IsSelected", typeof(bool), typeof(LeftMenuItem), new PropertyMetadata(default(bool)));

        public bool IsSelected
        {
            get => (bool) GetValue(IsSelectedProperty);
            set => SetValue(IsSelectedProperty, value);
        }

        public LeftMenuItem()
        {
            InitializeComponent();
        }

        private void UIElement_OnLeftMouseUp(object sender, MouseButtonEventArgs e)
        {
            ClickCommand?.Execute(ClickCommandParameter);
        }
    }
}
