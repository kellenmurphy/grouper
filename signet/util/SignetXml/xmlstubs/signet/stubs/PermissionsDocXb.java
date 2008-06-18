/*
	$Header: /home/hagleyj/i2mi/signet/util/SignetXml/xmlstubs/signet/stubs/PermissionsDocXb.java,v 1.2 2008-06-18 01:21:39 ddonn Exp $

Copyright (c) 2008 Internet2, Stanford University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package signet.stubs;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * PermissionsDocXb - XML binder class to provide privilege information
 * for a given Subject (assignments received and proxies received)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PermissionsDocXb",
		namespace="http://www.internet2.edu/signet",
		propOrder = {
			"subject",
			"proxy",
			"permission"
		}
)
public class PermissionsDocXb
{
	@XmlElement(name="subject", required=true)
	protected SignetSubjectRefXb		subject;

	@XmlElement(name="permission", required=false)
	protected List<PrivilegeXb>	permission;

	@XmlElement(name="proxy", required=false)
	protected List<ProxyImplRefXb>		proxy;

}
