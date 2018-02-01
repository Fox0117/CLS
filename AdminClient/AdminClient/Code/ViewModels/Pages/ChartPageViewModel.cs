using System;
using System.Collections.ObjectModel;
using System.Linq;
using MVVM_Tools.Code.Providers;

namespace AdminClient.Code.ViewModels.Pages
{
    internal class ChartPageViewModel : ViewModelBase
    {
        internal class ChartItemModel
        {
            public int Count { get; }
            public DateTime Date { get; }

            public ChartItemModel(DateTime date, int count)
            {
                Count = count;
                Date = date;
            }
        }

        public ObservableCollection<ChartItemModel> ChartItems { get; } = new ObservableCollection<ChartItemModel>();

        public PropertyProvider<int> VerticalAxisMaximum { get; }

        public ChartPageViewModel()
        {
            VerticalAxisMaximum = CreateProviderWithNotify<int>(nameof(VerticalAxisMaximum));

            InitExamples();
        }

        private void InitExamples()
        {
            ChartItems.Add(new ChartItemModel(DateTime.Now.AddSeconds(-2000), 32));
            ChartItems.Add(new ChartItemModel(DateTime.Now, 32));

            VerticalAxisMaximum.Value = ChartItems.Max(it => it.Count) + 10;
        }
    }
}
