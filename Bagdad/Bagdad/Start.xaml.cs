using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;

namespace Bagdad
{
    public partial class Start : PhoneApplicationPage
    {

        public Start()
        {
            InitializeComponent();
        }

        private void PivotSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            if (PivotSlider != null)
            {
                PivotSlider.Value = double.Parse(PivotSlider.Value.ToString("0."));
                StartPivot.SelectedIndex = (int)(PivotSlider.Value - 1);
            }
        }

        private void StartPivot_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (PivotSlider != null)
            {
                PivotSlider.Value = (StartPivot.SelectedIndex + 1);
            }
        }
    }
}