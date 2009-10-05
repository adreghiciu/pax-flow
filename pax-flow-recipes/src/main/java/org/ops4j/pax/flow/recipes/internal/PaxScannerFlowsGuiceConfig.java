/*
 * Copyright 2009 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.flow.recipes.internal;

import com.google.inject.AbstractModule;
import static com.google.inject.name.Names.*;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.recipes.flow.paxscanner.ScanBundles;
import org.ops4j.pax.flow.recipes.flow.paxscanner.SyncBundlesWithScanner;
import org.ops4j.pax.scanner.ProvisionService;
import static org.ops4j.peaberry.Peaberry.*;
import static org.ops4j.peaberry.util.TypeLiterals.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class PaxScannerFlowsGuiceConfig
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( ProvisionService.class ).toProvider( service( ProvisionService.class ).single() );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( ScanBundles.Factory.class.getName() ) )
            .toProvider(
                service( ScanBundles.Factory.class )
                    .attributes( ScanBundles.Factory.attributes() )
                    .export()
            );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( SyncBundlesWithScanner.Factory.class.getName() ) )
            .toProvider(
                service( SyncBundlesWithScanner.Factory.class )
                    .attributes( SyncBundlesWithScanner.Factory.attributes() )
                    .export()
            );
    }

}