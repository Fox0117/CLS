//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.20 at 06:36:20 PM MSK 
//


package ru.lumberjackcode.vacls.server.applicationparams;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _VaclsServerParams_QNAME = new QName("", "VaclsServerParams");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VaclsServerParams }
     * 
     */
    public VaclsServerParams createVaclsServerParams() {
        return new VaclsServerParams();
    }

    /**
     * Create an instance of {@link SystemParams }
     * 
     */
    public SystemParams createSystemParams() {
        return new SystemParams();
    }

    /**
     * Create an instance of {@link ConnectionParams }
     * 
     */
    public ConnectionParams createConnectionParams() {
        return new ConnectionParams();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VaclsServerParams }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "VaclsServerParams")
    public JAXBElement<VaclsServerParams> createVaclsServerParams(VaclsServerParams value) {
        return new JAXBElement<VaclsServerParams>(_VaclsServerParams_QNAME, VaclsServerParams.class, null, value);
    }

}
