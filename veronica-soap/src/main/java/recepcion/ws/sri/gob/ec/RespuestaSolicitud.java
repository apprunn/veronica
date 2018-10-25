
package recepcion.ws.sri.gob.ec;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para respuestaSolicitud complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="respuestaSolicitud">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="estado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="comprobantes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://ec.gob.sri.ws.recepcion}comprobante" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaSolicitud", propOrder = {
    "estado",
    "comprobantes"
})
public class RespuestaSolicitud {

    protected String estado;
    protected RespuestaSolicitud.Comprobantes comprobantes;

    /**
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

    /**
     * Obtiene el valor de la propiedad comprobantes.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaSolicitud.Comprobantes }
     *     
     */
    public RespuestaSolicitud.Comprobantes getComprobantes() {
        return comprobantes;
    }

    /**
     * Define el valor de la propiedad comprobantes.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaSolicitud.Comprobantes }
     *     
     */
    public void setComprobantes(RespuestaSolicitud.Comprobantes value) {
        this.comprobantes = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://ec.gob.sri.ws.recepcion}comprobante" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "comprobante"
    })
    public static class Comprobantes {

//        @XmlElement(namespace = "http://ec.gob.sri.ws.recepcion")
        protected List<Comprobante> comprobante;

        /**
         * Gets the value of the comprobante property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the comprobante property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getComprobante().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Comprobante }
         * 
         * 
         */
        public List<Comprobante> getComprobante() {
            if (comprobante == null) {
                comprobante = new ArrayList<Comprobante>();
            }
            return this.comprobante;
        }

    }

}
