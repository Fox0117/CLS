using System;

namespace AdminClient.Code.ViewModels.Controls
{
    internal class LeftMenuItemViewModel : ViewModelBase
    {
        public string Title
        {
            get => _title;
            set => SetProperty(ref _title, value);
        }
        private string _title;

        public Uri PageUri
        {
            get => _pageUri;
            set => SetProperty(ref _pageUri, value);
        }
        private Uri _pageUri;

        public bool IsSelected
        {
            get => _isSelected;
            set => SetProperty(ref _isSelected, value);
        }
        private bool _isSelected;
    }
}
