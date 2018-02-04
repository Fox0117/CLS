using System;
// ReSharper disable InconsistentNaming

namespace AdminClient.Code.Responses
{
    internal class EntriesRangeResponse
    {
        public string error_message;
        public DateTime minimum_date;
        public DateTime maximum_date;
    }
}
