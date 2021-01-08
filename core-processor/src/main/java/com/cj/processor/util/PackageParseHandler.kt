package com.cj.processor.util

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler


/**
 * Author:chris - jason
 * Date:2019-12-26.
 * Package:com.cj.processor.util
 */
class PackageParseHandler : DefaultHandler() {

    var packageName:String=""

    override fun startDocument() {
        super.startDocument()
        println("SAX START")
    }

    override fun endDocument() {
        super.endDocument()
        println("SAX END")
    }

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        super.startElement(uri, localName, qName, attributes)

        if(qName.equals("manifest")){
            var num = attributes!!.length
            for (i in 0..num step 1){
                if(attributes.getQName(i) == "package"){
                    this.packageName = attributes.getValue(i)
                    break
                }
            }
        }


    }

}