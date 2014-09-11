using System;
using System.Collections.Generic;
using System.ComponentModel.Design.Serialization;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.Text;
using DataServiceTester.ServiceReference1;


namespace DataServiceTester
{
    class Program
    {
        
        static void Main(string[] args)
        {
            BasicHttpBinding binding = new BasicHttpBinding();
            EndpointAddress add=new EndpointAddress("http://localhost:8085/Coalesce.DataService/CoalesceDataService");
            
            CoalesceDataServiceClient svc= new CoalesceDataServiceClient(binding,add);
            //Console.WriteLine(svc.("765997c2-f47d-476a-aca9-03a579d7a063"));

            string p1 = "Fire team";
            string p2 = "Echelon";
            string[] rpn = svc.getEntityXMLKeys(p1,p2 );
            Console.Write("Retrieving Object Keys for {0} and {1}\n",p1,p2);
            foreach (string ss in rpn)
            {
                Console.WriteLine(ss);
            } Console.Read();
            Console.Read();


            /*******************************************************************************************************************************
             * This does return multiple results, like getEntityXmlByIdType() does but I am exceeding the message size.  
             * temporary fix is to return the first hit.
             ********************************************************************************************************************************/
  
            string p3 = "trexunit";
            string vpn = svc.getEntityXMLByName(p3,p1,p2);
            Console.Write("Retrieving Entity XML for {0}, {1} and {2}\n", p3, p1, p2);
            Console.WriteLine(vpn);
            Console.Read();
            
            Console.WriteLine(svc.getFieldValue("0c250715-cfea-43f5-926e-7f001ade8bfb"));

            var p = new jEntity();
            try
            {
                //p = svc.getXPath("559f0031-6e7d-438f-a8c8-009c947b7326", "entity", "", "");
            }
            catch (CommunicationException ex)
            {
                Console.WriteLine(ex.Message);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
            Console.WriteLine("bub");
            
        }
    }
}
