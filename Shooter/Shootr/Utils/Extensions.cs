using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bagdad.Utils
{
    public static class Extensions
    {
        public static System.Threading.Tasks.Task<System.IO.Stream> GetRequestStreamAsync(this System.Net.WebRequest wr)
        {
            if (wr.ContentLength < 0)
            {
                throw new InvalidOperationException("The ContentLength property of the WebRequest must first be set to the length of the content to be written to the stream.");
            }

            return Task<System.IO.Stream>.Factory.FromAsync(wr.BeginGetRequestStream, wr.EndGetRequestStream, null);
        }

        public static System.Threading.Tasks.Task<System.Net.WebResponse> GetResponseAsync(this System.Net.WebRequest wr)
        {
            try
            {
                return Task<System.Net.WebResponse>.Factory.FromAsync(wr.BeginGetResponse, wr.EndGetResponse, null);
            }
            catch (Exception e)
            {
                throw new Exception("ServiceCommunication - Extension: " + e.Message, e);
            }
        }
    }
}
