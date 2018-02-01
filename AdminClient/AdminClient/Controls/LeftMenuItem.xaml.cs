using System;
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
            "ClickCommandParameter", typeof(Uri), typeof(LeftMenuItem), new PropertyMetadata(default(Uri)));

        public Uri ClickCommandParameter
        {
            get => (Uri) GetValue(ClickCommandParameterProperty);
            set => SetValue(ClickCommandParameterProperty, value);
        }

        public LeftMenuItem()
        {
            InitializeComponent();
        }

        private void UIElement_OnMouseUp(object sender, MouseButtonEventArgs e)
        {
            ClickCommand?.Execute(ClickCommandParameter);
        }
    }
}
