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
        private static readonly string EntriesAddress;
        private static readonly string JavaScriptAddress;

        private static readonly JsonConverter ResponseConverter = new ResponseJsonConverter();

        static DatabaseModel()
        {
            var appSettings = AppSettings.GetInstanse();

            string startAddress = appSettings.SiteStartPath + "/";
            EntriesAddress = startAddress + appSettings.SiteEntriesMethod + "/";
            JavaScriptAddress = startAddress + appSettings.SiteJsMethod + "/";
        }

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
                        { "startDate", DateToString(startDate) },
                        { "endDate",   DateToString(endDate) }
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
            var textResponse = SendPostToString(JavaScriptAddress);

            return DeserializeJson<ScriptResponse>(textResponse);
        }

        public Task<ScriptResponse> GetScriptAsync(CancellationToken cancellationToken = default)
        {
            return Task.Run(() => GetScript(), cancellationToken);
        }

        public void SendScript(string scriptText)
        {
            SendPostToString(
                JavaScriptAddress,
                new NameValueCollection
                {
                    { "javascriptStatus", "upload" }
                },
                Encoding.UTF8.GetBytes(scriptText)
            );
        }

        public Task SendScriptAsync(string scriptText, CancellationToken cancellationToken = default)
        {
            return Task.Run(() => SendScript(scriptText), cancellationToken);
        }

        private static string DateToString(DateTime date)
        {
            return date.ToString("dd.MM.yyyy HH:mm");
        }

        private static T DeserializeJson<T>(string jsonString)
        {
            return JsonConvert.DeserializeObject<T>(jsonString, ResponseConverter);
        }

        private static string SendPostToString(string uri, NameValueCollection items = null, byte[] data = null)
        {
            return Encoding.UTF8.GetString(SendPost(uri, items, data));
        }

        private static byte[] SendPost(string uri, NameValueCollection items = null, byte[] data = null)
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

                return client.UploadData(uri, data ?? new byte[0]);
            }
        }
    }
}
