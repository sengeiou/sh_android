using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public abstract class BaseModelJsonConstructor
    {

        abstract protected String GetEntityName();
        public string ConstructOperation(String opsData, String operation, String searchParams, int offset, int nItems)
        {
            return "\"ops\":[{\"data\":[{" + opsData + "}],\"metadata\":{\"items\": " + nItems + ((offset != 0) ? ",\"offset\":" + offset : "") + ",\"TotalItems\":null,\"operation\":\"" + operation + "\"," + searchParams + ",\"entity\":\"" + GetEntityName() + "\"}}]";
        }

        abstract public Task<string> ConstructFilter(string conditionDate);

    }
}
