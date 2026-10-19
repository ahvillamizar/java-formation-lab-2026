package com.indra.catalog.suppliers.web;

import com.indra.catalog.suppliers.domain.Supplier;
import com.indra.catalog.suppliers.web.dto.CreateSupplierRequest;
import com.indra.catalog.suppliers.web.dto.SupplierResponse;
import org.springframework.stereotype.Component;

@Component
public class SupplierWebMapper {

    private SupplierWebMapper() {
    }

    static  Supplier toDomain(CreateSupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.name());
        supplier.setTaxId(request.taxId());
        supplier.setEmail(request.email());
        return supplier;
    }

    static  SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(supplier.getId(), supplier.getName(), supplier.getTaxId(), supplier.getEmail());
    }
}
