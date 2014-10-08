using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.IO.IsolatedStorage;
using System.Net;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media.Imaging;

namespace Bagdad.Utils
{
    public class UserImageManager
    {
        int idUser;
        bool multipleImage = false;
        Queue<String> userUrlImage = new Queue<string>();

        public void Enqueue(String newItem)
        {
            userUrlImage.Enqueue(newItem);
        }

        public void SaveMultipleImages()
        {
            try
            {
                multipleImage = true;
                if (userUrlImage.Count > 0)
                {
                    var nextItem = userUrlImage.Dequeue();

                    var dividedItem = nextItem.Split('♠');
                    
                    idUser = int.Parse(dividedItem[0]);
                    string url = dividedItem[1];
                    if (!IsolatedStorageFile.GetUserStoreForApplication().FileExists(idUser + ".jpg"))
                    {
                        Debug.WriteLine("SAVING IMAGE: " + url + " FOR idUser: " + idUser);

                        var webClientImg = new WebClient();
                        webClientImg.OpenReadCompleted += new OpenReadCompletedEventHandler(client_OpenReadCompleted);
                        webClientImg.OpenReadAsync(new Uri(url, UriKind.Absolute));
                    }
                    else SaveMultipleImages();
                }
            }
            catch(Exception e)
            {
                Debug.WriteLine("E R R O R : SaveMultipleImages: " + e.Message);
            }
        }

        /// <summary>
        /// Save the image from the url in an IsolatedStorageFile with the userID as the name of the image
        /// </summary>
        /// <param name="url">URL where is the current image on Internet (Server)</param>
        /// <param name="userID">ID of the user that owns the image</param>
        public void SaveImageFromURL(String url, int userID)
        {
            try
            {
                multipleImage = false;
                idUser = userID;
                WebClient webClientImg = new WebClient();
                webClientImg.OpenReadCompleted += new OpenReadCompletedEventHandler(client_OpenReadCompleted);
                webClientImg.OpenReadAsync(new Uri(url, UriKind.Absolute));
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : SaveImageFromURL: " + e.Message);
            }
        }

        void client_OpenReadCompleted(object sender, OpenReadCompletedEventArgs e)
        {
            if (e.Error == null)
            {
                bool isSpaceAvailable = IsSpaceIsAvailable(e.Result.Length);
                if (isSpaceAvailable)
                {
                    Deployment.Current.Dispatcher.BeginInvoke(() =>
                    {
                        SaveToJpeg(e.Result, idUser);
                    });
                }
                else
                {
                    Debug.WriteLine("W A R N I N G : client_OpenReadCompleted: You are running low on storage space on your phone. Hence the image will be loaded from the internet and not saved on the phone.");
                }
            }
        }

        private bool IsSpaceIsAvailable(long spaceReq)
        {
            try
            {
                using (var store = IsolatedStorageFile.GetUserStoreForApplication())
                {
                    long spaceAvail = store.AvailableFreeSpace;
                    if (spaceReq > spaceAvail)
                    {
                        return false;
                    }
                    return true;
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : IsSpaceIsAvailable: " + e.Message);
            }
            return false;
        }

        private void SaveToJpeg(Stream stream, int userID)
        {
            try
            {
                using (IsolatedStorageFile iso = IsolatedStorageFile.GetUserStoreForApplication())
                {
                    using (IsolatedStorageFileStream isostream = iso.CreateFile(userID + ".jpg"))
                    {
                        BitmapImage bitmap = new BitmapImage();
                        bitmap.SetSource(stream);
                        WriteableBitmap wb = new WriteableBitmap(bitmap);
                        // Encode WriteableBitmap object to a JPEG stream. 
                        System.Windows.Media.Imaging.Extensions.SaveJpeg(wb, isostream, wb.PixelWidth, wb.PixelHeight, 0, 85);
                        isostream.Close();
                        if (multipleImage) SaveMultipleImages();
                    }
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : SaveToJpeg: " + e.Message);
            }
        }

        /// <summary>
        /// Returns the current stored image of a user. If there is no image stored returns Null
        /// </summary>
        /// <param name="userID">ID of the image owner</param>
        /// <returns>BitmapImage if image exist, else return null</returns>
        public BitmapImage GetUserImage(int userID)
        {
            byte[] data;

            try
            {
                if (IsolatedStorageFile.GetUserStoreForApplication().FileExists(idUser + ".jpg"))
                {
                    using (IsolatedStorageFile isf = IsolatedStorageFile.GetUserStoreForApplication())
                    {
                        using (IsolatedStorageFileStream isfs = isf.OpenFile(userID + ".jpg", FileMode.Open, FileAccess.Read))
                        {
                            data = new byte[isfs.Length];
                            isfs.Read(data, 0, data.Length);
                            isfs.Close();
                        }
                    }
                    MemoryStream ms = new MemoryStream(data);
                    BitmapImage bi = new BitmapImage();
                    bi.SetSource(ms);
                    return bi;
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("E R R O R : GetUserImage: " + e.Message);
            }
            return null;
        }

        public string GetUserImagename(int idUser)
        {
            string retorn = String.Empty;
            try {
                retorn = IsolatedStorageFile.GetUserStoreForApplication().ToString() + idUser + ".jpg";
            }
            catch { }
            return retorn;
        }
    }
}
