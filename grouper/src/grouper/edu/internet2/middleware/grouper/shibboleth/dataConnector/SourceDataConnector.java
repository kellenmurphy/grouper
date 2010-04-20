/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.internet2.middleware.grouper.shibboleth.dataConnector;

import java.util.Date;
import java.util.Set;

import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.dataConnector.DataConnector;
import edu.internet2.middleware.subject.Source;
import edu.internet2.middleware.subject.Subject;

/**
 * An extension to a Shibboleth {@link DataConnector} which provides the ability to return
 * the identifiers of all objects. This 'Source' might resemble a Subject API
 * {@link Source} and its {@link Subject}s.
 */
public interface SourceDataConnector extends DataConnector {

  /**
   * Return the identifiers of all objects. The identifiers are destined for provisioning,
   * consequently, the order returned should accommodate that need.
   * 
   * @return the possibly empty but never null set of identifiers
   */
  Set<String> getAllIdentifiers();

  /**
   * Return the identifiers of all objects updated after the given time. see {@link
   * getAllIdentifers()}.
   * 
   * @param updatedSince
   *          the <code>Date</code> the returned identifiers were modified after
   * @return the possibly empty but never null set of identifiers updated since the given
   *         time
   */
  Set<String> getAllIdentifiers(Date updatedSince);
}
