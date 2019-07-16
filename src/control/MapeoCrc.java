package control;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

import modelo.TJSON;

public class MapeoCrc extends AbstractTransformation {
	@Override
	public void transform(TransformationInput input, TransformationOutput out) throws StreamTransformationException {
		executeIn(input.getInputPayload().getInputStream(),out.getOutputPayload().getOutputStream());
	}

	public void executeIn(InputStream is, OutputStream outs) {
		Element newelement, root;
		long valorcrc;
		DocumentBuilderFactory dbFactory;
		DocumentBuilder dBuilder;
		Document doc;
		Node node;
		try {
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			root = doc.getDocumentElement();
			ArrayList<TJSON> al = new ArrayList<>();	
			nodes(root, al);
			//al.forEach((TJSON o) -> System.out.println(o.toString()));
			valorcrc = DaCRC(al, doc.getElementsByTagName("id_usuario").toString());
			//insertamos los datos en el node crc
			if((node = doc.getDocumentElement().getElementsByTagName("crc").item(0))!=null)
			{
				node.setTextContent(valorcrc+"");
			}else{
				newelement=doc.createElement("crc");
				newelement.setTextContent(valorcrc+"");
				root.appendChild(newelement);
			}

			
		
			//imprimimos el resultado
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outs);
			
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void nodes(Element element, ArrayList<TJSON> al) {
		TJSON tjson = new TJSON();
		NodeList nlist;
		Node nNode = element;
		if (nNode.hasChildNodes()) {
			nlist = element.getChildNodes();

			for (int i = 0; i < nlist.getLength(); i++) {
				nNode = nlist.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					nodes(eElement, al);
					if (nNode.getNodeName() != "request" && nNode.getNodeName() != "lineas") {
						tjson = new TJSON(eElement.getNodeName(), eElement.getTextContent());
						al.add(tjson);

					}
				}

			}
		}

	}

	public long DaCRC(ArrayList<TJSON> listjson, String Usuario) {

		String CadenaCRC = "";

		long ValorCRC = -9999;
		try {

			int LongitudUsuario = Usuario.length();

			int Contador = -1;
			int TotalParametrosCabecera = listjson.size();
			for (int j = 0; j < TotalParametrosCabecera; j++) {
				TJSON Par = listjson.get(j);
				boolean Vale = true;
				if (Par.Key.equalsIgnoreCase("crc"))
					Vale = false;
				if (Par.Key.equalsIgnoreCase("id_usuario"))
					Vale = false;
				if (Par.Key.equalsIgnoreCase("iDOC"))
					Vale = false;
				if (Vale == true) {
					CadenaCRC += " ";
					String Cadena = Par.Key + Par.Valor;

					int LongitudCadena = Cadena.length();
					for (int i = 0; i < LongitudCadena; i++) {
						int Caracter = Cadena.charAt(i);
						if ((Caracter >= 'A') && (Caracter <= 'z')) {
							Contador++;
							if (Contador >= LongitudUsuario)
								Contador = 0;
							long ValorAuxiliar = Usuario.charAt(Contador);
							long ValorAuxiliar2 = ValorAuxiliar * Caracter;
							ValorCRC += ValorAuxiliar2;
							String Prefijo = "";
							if (i > 0)
								Prefijo += " + ";
							String CadenaAuxiliar = Cadena.substring(i, i + 1);
							CadenaCRC += CadenaAuxiliar;
							CadenaAuxiliar = Prefijo + CadenaAuxiliar + "(" + Long.toString(ValorAuxiliar) + "x"
									+ Long.toString(Caracter) + ")";

						}
					}
				}
			}
		} catch (Exception e) {
		}

		CadenaCRC = CadenaCRC.trim();

		return ValorCRC;
	}

}
