using System.Windows;
using AdminClient.Resources.Localizations;

namespace AdminClient.Code.Utils
{
    internal static class MessageUtils
    {
        public static void ShowExclamation(string message)
        {
            MessageBox.Show(
                message, StringResources.Error_Title,
                MessageBoxButton.OK, MessageBoxImage.Exclamation
            );
        }
    }
}
