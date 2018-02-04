using System;
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

            //var date = DateTime.ParseExact(stringDate, "dd'.'MM'.'yyyy hh:mm", CultureInfo.InvariantCulture, DateTimeStyles.None);

            return new DateTime(1, 1, 1);
        }

        public override bool CanConvert(Type objectType)
        {
            return objectType == typeof(DateTime);
        }
    }
}
