using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.SqlTypes;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.ServiceModel;
using System.Text;
using System.Windows.Forms;
using System.Xml;
using System.Xml.Linq;
using System.Xml.Serialization;
using wDataServiceTester.ServiceReference1;
using getEntityRequest = wDataServiceTester.ServiceReference1.getEntityRequest;

namespace wDataServiceTester
{
    public partial class Form1 : Form
    {
        //  This bdinging is causing an issue with the SOAP 1.1 and SOAP 1.2
        BasicHttpBinding  binding = new BasicHttpBinding();

        EndpointAddress add = new EndpointAddress("http://localhost:8085/DataService/CoalesceDataService");
        private CoalesceDataServiceClient svc;
        private string sbuff = "";
        private bool bLoading = true;
        private String[] rsx;
        public Form1()
        {
            InitializeComponent();
            
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            
            svc =new CoalesceDataServiceClient(binding,add);
            
        }


        public Object xmlDeserializeMemoryStream(string filename)
        {
            Object bRetVal = null;
            try
            {
                
                //byte[] bArray = new byte[filename.Length];
                //MemoryStream ms = new MemoryStream(bArray);
                //jEntity xmlMission=new jEntity();
                //TextReader xmlRDR = new StreamReader(ms, System.Text.Encoding.UTF8, true);
                //((MemoryStream)ms).Position = 0;
                //XmlSerializer xmlDES = new XmlSerializer(typeof(jEntity));
                //xmlMission = (jEntity)xmlDES.Deserialize(xmlRDR);
                
                //xmlRDR.Close();
                return bRetVal;

            }
            catch (Exception ex)
            {
                ShowMessage(ex);
                return bRetVal;
            }
        }
        private void button1_Click(object sender, EventArgs e)
        {

            try
            {
                if (textBox1.TextLength > 0)
                {
                    richTextBox1.Text=svc.getEntity(textBox1.Text);
                }
            }
            catch (Exception ex)
            {
                ShowMessage(ex);
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (button2.Text == "Format XML")
            {
                button2.Text = "RAW";
                sbuff = richTextBox1.Text;
                richTextBox1.Text = FormatXml(richTextBox1.Text);
                txtLength.Text = richTextBox1.Text.Length.ToString();
            }
            else if (button2.Text == "RAW")
            {
                button2.Text = "Format XML";
                richTextBox1.Text = sbuff;
                txtLength.Text = richTextBox1.Text.Length.ToString();
            }
        }

        private string FormatXml(String Xml)
        {
            try
            {
                XDocument doc = XDocument.Parse(Xml);
                return doc.ToString();
            }
            catch (Exception)
            {
                return Xml;
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            try
            {
                cmbMessageCnt.Items.Clear();
                rsx=svc.getEntityXMLKeys(textBox2.Text.Trim(), textBox3.Text.Trim());
                if (textBox2.TextLength > 0 && textBox3.TextLength > 0)
                {
                    if (rsx.Length > 0)
                    {
                        int i = 0;
                        foreach (var s in rsx)
                        {
                            cmbMessageCnt.Items.Add(i);
                            i++;
                        }
                        textBox4.Text = rsx.Length.ToString();
                        cmbMessageCnt.SelectedIndex = 0;
                        bLoading = false;
                    }
                }
            }
            catch (CommunicationException ex)
            {
                ShowMessage(ex);
            }
            catch (Exception ex)
            {
                ShowMessage(ex);
            }
        }

        private void ShowMessage(Exception communicationException)
        {
            MessageBox.Show("Error Message: " + communicationException.Message, "Tester", MessageBoxButtons.OK,
                MessageBoxIcon.Error);
            Console.WriteLine(communicationException.Message);
        }


        private void ShowUserMessage(String msg)
        {
            MessageBox.Show("Error Message: " + msg, "Tester", MessageBoxButtons.OK,
                MessageBoxIcon.Error);
        }
        private void cmbMessageCnt_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (bLoading == false)
            {
                sbuff = rsx[cmbMessageCnt.SelectedIndex];
                richTextBox1.Text = sbuff;
            }
        }

        private void button4_Click(object sender, EventArgs e)
        {
            if (textBox7.TextLength>0 && textBox6.TextLength > 0 && textBox5.TextLength > 0)
            {
                sbuff = svc.getEntityXMLByName(textBox7.Text.Trim(), textBox6.Text.Trim(), textBox5.Text.Trim());
                richTextBox1.Text = sbuff;
            }
        }

        private void button5_Click(object sender, EventArgs e)
        {
            if (txtGetValue.TextLength > 0)
            {
                richTextBox1.Text = svc.getFieldValue(txtGetValue.Text.Trim());
            }
        }

        private void button7_Click(object sender, EventArgs e)
        {
            
            try
            {
                if (svc.setEntity("559f0031-6e7d-438f-a8c8-009c947b732a", CoalesceTypeInstances.TestMission))
                    ShowUserMessage("Saved Update.");

                
            }
            catch (XmlException ex)
            {
                ShowMessage(ex);
            }
            catch (Exception ex)
            {
                ShowMessage(ex);
            }
            
        }


        public static SqlXml ConvertString2SqlXml(string xmlData)
        {
            UTF8Encoding encoding = new UTF8Encoding();
            MemoryStream m = new MemoryStream(encoding.GetBytes(xmlData));
            return new SqlXml(m);
        }
    }
}
