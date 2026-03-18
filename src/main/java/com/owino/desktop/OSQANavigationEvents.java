package com.owino.desktop;
/*
 * Copyright (C) 2026 Samuel Owino
 *
 * OSQA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSQA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OSQA.  If not, see <https://www.gnu.org/licenses/>.
 */
import java.util.UUID;
import com.owino.core.OSQAModel.OSQAProduct;
import com.owino.core.OSQAModel.OSQAFeature;
public sealed interface OSQANavigationEvents {
    record OpenDashboardEvent() implements OSQANavigationEvents {}
    record OpenFeatureFormEvent(String featureUuid,boolean isEditMode) implements OSQANavigationEvents {
        public OpenFeatureFormEvent(){
            this(UUID.randomUUID().toString(),false);
        }
    }
    record OpenFeatureDetailedViewEvent(OSQAFeature selectedFeature, OSQAProduct product) implements OSQANavigationEvents {}
    record OpenFeaturesListViewEvent(OSQAProduct selectedProduct) implements OSQANavigationEvents {}
    record ToggleShowVerificationButtonEvent(boolean show) implements OSQANavigationEvents {}
    record ShowVerificationFormEvent() implements OSQANavigationEvents {}
    record ResetVerificationsEvent() implements  OSQANavigationEvents {}
    record OpenProductFormEvent(boolean isEditMode,OSQAProduct product) implements  OSQANavigationEvents {
        public OpenProductFormEvent(){this(false, null);}
    }
    record OpenProductsListEvent() implements  OSQANavigationEvents {}
}
