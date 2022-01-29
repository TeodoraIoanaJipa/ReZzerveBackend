package com.teo.foodzzzbackend.controller;

import com.teo.foodzzzbackend.model.*;
import com.teo.foodzzzbackend.security.payload.response.MessageResponse;
import com.teo.foodzzzbackend.service.ManagerService;
import com.teo.foodzzzbackend.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    private Logger logger = Logger.getLogger(ManagerController.class.getName());

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ManagerService managerService;

    @GetMapping("/restaurant")
    @CrossOrigin
    public Restaurant getRestaurantData(@RequestParam String managerId) {
        return (restaurantService.findRestaurantByManagerId(managerId));
    }

    @GetMapping("/restaurant/kitchen-types")
    @CrossOrigin
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    public List<KitchenType> getKitchenTypes() {
        return (managerService.findAllKitchenTypes());
    }

    @GetMapping("/restaurant/local-types")
    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public List<LocalType> getLocalTypes() {
        return (managerService.findAllLocalTypes());
    }

    @PostMapping(path = "/restaurant/update", consumes = "application/json")
    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public Restaurant updateRestaurant(@RequestBody RestaurantUpdateDTO restaurantDTO) {
        return managerService.updateRestaurant(restaurantDTO);
    }

    @PostMapping(path = "/restaurant/save", consumes = "application/json")
    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveRestaurant(@RequestBody RestaurantUpdateDTO restaurantDTO,
                                            @RequestParam String managerEmail) {
        ResponseEntity responseEntity = managerService.checkIfManager(managerEmail);
        if (responseEntity.getStatusCode().value() == 200) {
            User user = (User) responseEntity.getBody();
            managerService.saveUserManager(user);
            restaurantDTO.setManagerId(user.getId());
            return ResponseEntity.ok(managerService.saveRestaurant(restaurantDTO));
        } else {
            return responseEntity;
        }
    }

    @PostMapping(path = "/restaurant/tables/save", consumes = "application/json")
    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveTables(@RequestBody List<TableFormDTO> tables,
                                        @RequestParam String restaurantId,
                                        @RequestParam String width,
                                        @RequestParam String height) {
        try {
            List<TableForm> tablesSaved = managerService.saveTables(tables, restaurantId, width, height);
            return ResponseEntity.ok(tablesSaved);
        } catch (Exception exception) {
            logger.log(Level.WARNING, "/restaurant/tables/save could not save tables. " + exception.getMessage());
            return new ResponseEntity("Could not save tables.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/reservations/pending")
    @CrossOrigin
    public List<Reservation> getPendingReservations(@RequestParam String restaurantId) {
        return (restaurantService.findAllReservationsByRestaurantId(restaurantId));
    }

    @PutMapping("/reservations/decline")
    @CrossOrigin
    public ResponseEntity<String> updateReservationStatusToDeclined(@RequestParam String reservationId) throws ParseException {
        try {
            managerService.updateReservationStatus(reservationId, ReservationStatus.DECLINED);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception exception) {
            logger.log(Level.WARNING, "/reservations/decline could not decline reservation. " + exception.getMessage());
            return new ResponseEntity("Could not decline reservation.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/reservations/accept")
    @CrossOrigin
    public ResponseEntity<String> updateReservationStatusToAccepted(@RequestParam String reservationId) throws ParseException {
        try {
            managerService.updateReservationStatus(reservationId, ReservationStatus.ACCEPTED);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception exception) {
            logger.log(Level.WARNING, "/reservations/decline could not accept reservation. " + exception.getMessage());
            return new ResponseEntity("Could not accept reservation.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(path = "/restaurant/upload-file")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @CrossOrigin
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam String restaurantId) {
        if (managerService.checkFilesNumberSmallerThanTen(restaurantId) < 10) {
            Images dbFile = managerService.storeFile(file, restaurantId);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/downloadFile/")
                    .path(dbFile.getId().toString())
                    .toUriString();

            return ResponseEntity.ok(new UploadFileResponse(dbFile.getFileName(), fileDownloadUri,
                    file.getContentType(), file.getSize()));
        } else {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Numarul maxim de imagini a fost atins."));
        }
    }

    @GetMapping("/restaurant/images")
    @CrossOrigin
    public List<Images> getImages(@RequestParam String restaurantId) {
        return (managerService.findImagesByRestaurantId(restaurantId));
    }

    @GetMapping("/restaurant/download-file")
    @CrossOrigin
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileId) {
        Images dbFile = managerService.getFile(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dbFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
                .body(new ByteArrayResource(dbFile.getData()));

    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/restaurant/delete-file")
    @CrossOrigin
    public ResponseEntity<?> deleteFile(@RequestParam String fileId) {
        managerService.deleteFile(fileId);
        return ResponseEntity.ok(new MessageResponse("Imagine stearsa cu succes!"));
    }
}
