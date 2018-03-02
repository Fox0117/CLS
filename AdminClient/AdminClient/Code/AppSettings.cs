using System.IO;
using System.Reflection;
using System.Text;
using Newtonsoft.Json;

namespace AdminClient.Code
{
    internal class AppSettings
    {
        public string SiteStartPath { get; set; }
        public string SiteEntriesMethod { get; set; }
        public string SiteJsMethod { get; set; }

        public bool TestDataEnabled { get; set; }

        private static AppSettings _instanse;

        public static AppSettings GetInstanse()
        {
            if (_instanse != null)
                return _instanse;

            string exeFolder = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location) ?? string.Empty;
            string appSettingsFile = Path.Combine(exeFolder, "appsettings.json");

            return _instanse = JsonConvert.DeserializeObject<AppSettings>(File.ReadAllText(appSettingsFile, Encoding.UTF8));
        }
    }
}
