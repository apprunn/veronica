
package autorizacion.ws.sri.gob.ec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para autorizacionComprobanteLoteResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="autorizacionComprobanteLoteResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaAutorizacionLote" type="{http://ec.gob.sri.ws.autorizacion}respuestaLote" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autorizacionComprobanteLoteResponse", propOrder = {
    "respuestaAutorizacionLote"
})
public class AutorizacionComprobanteLoteResponse {

    @XmlElement(name = "RespuestaAutorizacionLote")
    protected RespuestaLote respuestaAutorizacionLote;

    /**
     * Obtiene el valor de la propiedad respuestaAutorizacionLote.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaLote }
     *     
     */
    public RespuestaLote getRespuestaAutorizacionLote() {
        return respuestaAutorizacionLote;
    }

    /**
     * Define el valor de la propiedad respuestaAutorizacionLote.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaLote }
     *     
     */
    public void setRespuestaAutorizacionLote(RespuestaLote value) {
        this.respuestaAutorizacionLote = value;
    }

}
