using System;
using System.Threading;
using System.Threading.Tasks;
using AdminClient.Code.Responses;

namespace AdminClient.Code.Interfaces
{
    internal interface IDatabaseModel
    {
        EntriesRangeResponse GetEntriesRange();

        Task<EntriesRangeResponse> GetEntriesRangeAsync(CancellationToken cancellationToken = default);

        EntriesResponse GetEntries(DateTime startDate, DateTime endDate);

        Task<EntriesResponse> GetEntriesAsync(DateTime startDate, DateTime endDate, CancellationToken cancellationToken = default);

        ScriptResponse GetScript();

        Task<ScriptResponse> GetScriptAsync(CancellationToken cancellationToken = default);

        void SendScript(string scriptText);
        
        Task SendScriptAsync(string scriptText, CancellationToken cancellationToken = default);
    }
}