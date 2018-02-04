using System;
using System.Collections.Generic;
using System.Linq;
using AdminClient.Code.Responses;

namespace AdminClient.Code.Utils
{
    internal static class EntriesUtils
    {
        public static List<EntriesResponse.OneEntry> GenEntries(int count = 30)
        {
            var random = new Random();

            var list = new List<EntriesResponse.OneEntry>();

            for (int i = 0; i < count; i++)
            {
                list.Add(new EntriesResponse.OneEntry
                {
                    date = DateTime.Now.AddMinutes(i + random.Next(-10000, 10000))
                });
            }

            return list;
        }

        public static List<(DateTime date, int count)> GroupByMinute(IList<EntriesResponse.OneEntry> entries)
        {
            return GroupEntries(entries, date => new DateTime(date.Year, date.Month, date.Day, date.Hour, date.Minute, 0));
        }

        public static List<(DateTime date, int count)> GroupByHour(IList<EntriesResponse.OneEntry> entries)
        {
            return GroupEntries(entries, date => new DateTime(date.Year, date.Month, date.Day, date.Hour, 0, 0));
        }

        public static List<(DateTime date, int count)> GroupByDay(IList<EntriesResponse.OneEntry> entries)
        {
            return GroupEntries(entries, date => new DateTime(date.Year, date.Month, date.Day));
        }

        public static List<(DateTime date, int count)> GroupByMonth(IList<EntriesResponse.OneEntry> entries)
        {
            return GroupEntries(entries, date => new DateTime(date.Year, date.Month, 1));
        }

        private static List<(DateTime date, int count)> GroupEntries(IList<EntriesResponse.OneEntry> entries, Func<DateTime, DateTime> trunc)
        {
            if (entries.Count == 0)
                return new List<(DateTime date, int count)>();

            var result = new List<(DateTime date, int count)>();

            var currentDate = trunc(entries[0].date);
            int count = 1;

            foreach (var entry in entries.Skip(1))
            {
                var truncDate = trunc(entry.date);

                if (truncDate > currentDate)
                {
                    result.Add((currentDate, count));
                    count = 1;
                    currentDate = truncDate;
                }
                else
                {
                    count++;
                }
            }

            result.Add((currentDate, count));

            return result;
        }
    }
}
