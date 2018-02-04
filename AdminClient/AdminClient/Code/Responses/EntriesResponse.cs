using System;
using System.Collections.Generic;
// ReSharper disable InconsistentNaming

namespace AdminClient.Code.Responses
{
    internal class EntriesResponse
    {
        internal class OneEntry
        {
            public string identifier;
            public DateTime date;
        }

        public string status;
        public string error_message;
        public List<OneEntry> entries;
    }
}
