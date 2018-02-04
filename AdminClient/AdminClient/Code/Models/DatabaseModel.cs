using System;
using System.Collections.Specialized;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using AdminClient.Code.Interfaces;
using AdminClient.Code.JsonConverters;
using AdminClient.Code.Responses;
using Newtonsoft.Json;

namespace AdminClient.Code.Models
{
    internal class DatabaseModel : IDatabaseModel
    {
        private const string StartAddress = "http://capitangaga.redirectme.net:5000/";
        private const string EntriesAddress = StartAddress + "entries/";

        public EntriesRangeResponse GetEntriesRange()
        {
            var textResponse = SendPostToString(EntriesAddress);

            return DeserializeJson<EntriesRangeResponse>(textResponse);
        }

        public Task<EntriesRangeResponse> GetEntriesRangeAsync(CancellationToken cancellationToken = default)
        {
            return Task.Run(() => GetEntriesRange(), cancellationToken);
        }

        public EntriesResponse GetEntries(DateTime startDate, DateTime endDate)
        {
            var textResponse = 
                SendPostToString(
                    EntriesAddress, 
                    new NameValueCollection
                    {
                        { "startDate", "11.11.1111 11:11" },
                        { "endDate",   "11.11.1111 11:11" }
                    }
                );

            return DeserializeJson<EntriesResponse>(textResponse);
        }

        public Task<EntriesResponse> GetEntriesAsync(DateTime startDate, DateTime endDate, CancellationToken cancellationToken = default)
        {
            return Task.Run(() => GetEntries(startDate, endDate), cancellationToken);
        }

        public ScriptResponse GetScript()
        {
            throw new NotImplementedException();
        }

        public Task<ScriptResponse> GetScriptAsync(CancellationToken cancellationToken = default)
        {
            throw new NotImplementedException();
        }

        private static T DeserializeJson<T>(string jsonString)
        {
            return JsonConvert.DeserializeObject<T>(jsonString, new ResponseJsonConverter());
        }

        private static string SendPostToString(string uri, NameValueCollection items = null)
        {
            return SendPost(uri, items); //Encoding.UTF8.GetString(SendPost(uri, items));
        }

        private static string SendPost(string uri, NameValueCollection items = null)
        {
            using (var client = new WebClient())
            {
                if (items != null)
                {
                    foreach (string key in items.AllKeys)
                    {
                        client.Headers.Add(key, items[key]);
                    }
                }

                return client.UploadString(uri, "");
            }
        }
    }
}
