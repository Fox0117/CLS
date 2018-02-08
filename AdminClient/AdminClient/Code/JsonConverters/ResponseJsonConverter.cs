using System;
using System.Globalization;
using Bugsnag;
using Bugsnag.Clients;
using Newtonsoft.Json;

namespace AdminClient.Code.JsonConverters
{
    internal class ResponseJsonConverter : JsonConverter
    {
        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
            throw new NotImplementedException();
        }

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            var stringDate = (string) reader.Value;

            try
            {
                return DateTime.ParseExact(stringDate, "dd.MM.yyyy hh:mm", CultureInfo.InvariantCulture);
            }
            catch (FormatException ex)
            {
                WPFClient.NotifyAsync(
                    new FormatException($"Can't parse datetime. Source string: \"{stringDate}\"", ex),
                    Severity.Error
                );
                return new DateTime();
            }
        }

        public override bool CanConvert(Type objectType)
        {
            return objectType == typeof(DateTime);
        }
    }
}
