using Bagdad.Utils;
using Bagdad.ViewModels;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bagdad.Models
{
    public partial class Watch : BaseModelJsonConstructor
    {
        public override Task<int> SaveData(List<BaseModelJsonConstructor> models)
        {
            throw new NotImplementedException();
        }
    }
}
