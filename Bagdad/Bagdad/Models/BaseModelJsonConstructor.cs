using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json.Linq;

namespace Bagdad.Models
{
    public abstract class BaseModelJsonConstructor
    {

        abstract protected String GetOps();

        abstract protected String GetAlias(String operation);

        abstract protected String GetEntityName();

        virtual public string ConstructOperation(String operation, String searchParams, int offset, int nItems)
        {
            return "\"ops\":[{\"data\":[{" + GetOps() + "}],\"metadata\":{\"items\": " + nItems + ((offset != 0) ? ",\"offset\":" + offset : "") + ",\"TotalItems\":null,\"operation\":\"" + operation + "\"," + searchParams + ",\"entity\":\"" + GetEntityName() + "\"}}]";
        }

        virtual public string ConstructAlias(String operation)
        {
            return "\"alias\":"+ GetAlias(operation) + "";
        }

        abstract public Task<string> ConstructFilter(string conditionDate);

        abstract public List<BaseModelJsonConstructor> ParseJson(JObject job);

        abstract public Task<int> SaveData(List<BaseModelJsonConstructor> models);

    }
}
