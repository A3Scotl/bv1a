package com.benhvien1a.service;


import com.benhvien1a.dto.ServiceDTO;
import com.benhvien1a.model.Service;

import java.util.List;

public interface ServiceService {
    List<Service> getAllServices();
    Service getServiceById(Long id);
    Service createService(ServiceDTO request);
    Service updateService(Long id, ServiceDTO request);
    void deleteService(Long id);
    Service getServiceBySlug(String slug);
    List<Service> getAllActiveServices();
    void hideService(Long id);
}
