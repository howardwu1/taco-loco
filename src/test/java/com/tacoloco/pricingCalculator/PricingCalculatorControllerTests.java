package com.tacoloco.pricingCalculator;
import com.tacoloco.services.PricingCalculatorService;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;


import org.junit.jupiter.api.DisplayName;
                                

import com.tacoloco.model.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;

                                    
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value="test")
@AutoConfigureMockMvc
class PricingCalculatorControllerTests {

  @Autowired
	private MockMvc mockMvc;

  @MockBean
  private PricingCalculatorService service;

  // @BeforeAll
  // public void setup() {
  //   mockMvc.perform(put("/authenticate")
  //                   .contentType(MediaType.APPLICATION_JSON)
  //                   .content(mockJson))
  //       .andExpect(status().isOk());

  // }



  @Test
  @DisplayName("post /authenticate valid user info")
	public void postAuthenticateUserValid() throws Exception{
         
      String mockJson = "{\"username\":\"javainuse\", \"firstName\":\"Java\", \"lastName\": \"InUse\", \"password\": \"password\", \"matchingPassword\": \"password\"}";

      mockMvc.perform(post("/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mockJson))
        .andExpect(status().isOk());

	}


  @Test
  @DisplayName("post /authenticate invalid user info")
	public void postAuthenticateUserInvalid() throws Exception{
         
      String mockJson = "{\"username\":\"javainuse\", \"firstName\":\"Java\", \"lastName\": \"InUse\", \"password\": \"fake\", \"matchingPassword\": \"fake\"}";

      mockMvc.perform(post("/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mockJson))
        .andExpect(status().is(401));

	}


  @Test
  @DisplayName("put /insertCustomer valid user info")
	public void putInsertCustomerValid() throws Exception{
      
      String mockJson = "{\"username\":\"SirSnoopy\", \"firstName\":\"Joe\", \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
      
    //todo replace customers with users 
      doNothing().when(service).insertIntoCustomers(eq("SirSnoopy"), eq("Joe"), eq("Cool"), any(String.class));

      mockMvc.perform(put("/insertCustomer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mockJson))
        .andExpect(status().isOk());

      verify(service).insertIntoCustomers(eq("SirSnoopy"), eq("Joe"), eq("Cool"), any(String.class));

	}

//   @Test
//   @DisplayName("put /insertCustomer invalid user info first name int")
// 	public void putInsertInvalidFirstNameIntCustomer() throws Exception{
      
//       String mockJson = "{\"username\": \"SirSnoopy\", \"firstName\":2, \"lastName\": \"Cool\", \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoDubbaG\"}";
      
//       //todo replace customers with users 
      
//       mockMvc.perform(put("/insertCustomer")
//                     .contentType(MediaType.APPLICATION_JSON)
//                     .content(mockJson))
//         .andExpect(status().is(422));
      
//       verify(service, never()).insertIntoCustomers(any(), any(),any(), any());

// 	}

//   @Test
//   @DisplayName("put /insertCustomer non-matching password")
// 	public void putInsertInvalidNonMatchingPasswordCustomer() throws Exception{
      
//       String mockJson = "{\"username\": \"SirSnoopy\", \"firstName\":2, \"lastName\": \"Cool\",, \"password\": \"SnoopDoDubbaG\", \"matchingPassword\": \"SnoopDoggyDog\"}";
            
//       mockMvc.perform(put("/insertCustomer")
//                     .contentType(MediaType.APPLICATION_JSON)
//                     .content(mockJson))
//         .andExpect(status().is(422));
      
//       verify(service, never()).insertIntoCustomers(any(), any(),any(), any());

// 	}

//   @Test
//   @DisplayName("put /validatePassword")
// 	public void putValidatePasswordCustomer() throws Exception{
      
//       String mockJson = "{\"firstName\":\"Joe\", \"lastName\": \"Cool\",, \"password\": \"SnoopDoDubbaG\"}";


//       doNothing().when(service).checkPassword("Joe", "Cool", "SnoopDoDubbaG"); 

//       mockMvc.perform(put("/validateCustomer")
//                     .contentType(MediaType.APPLICATION_JSON)
//                     .content(mockJson))
//         .andExpect(status().is(200));
      
//        verify(service).checkPassword("Joe", "Cool", "SnoopDoDubbaG");


// 	}

//   @Test
//   @DisplayName("post /total valid order")
// 	public void getTotalValid() throws Exception{
      
//       String mockJson = "{\"veggie\":1}";
//       doReturn("2.50").when(service).getTotal(any(Order.class));

//       mockMvc.perform(post("/total")
//                     .contentType(MediaType.APPLICATION_JSON)
//                     .content(mockJson))
//         .andExpect(status().isOk())
//         .andExpect(content().string(containsString("2.50")));

// 	}

// 	@Test
//   @DisplayName("get / -- hello world")
// 	public void getHelloWorld() throws Exception {
      
//       doReturn("hello world").when(service).sayHello();

//       mockMvc.perform(get("/"))
//         .andExpect(status().isOk())
//         .andExpect(content().string(containsString("hello world")));

// 	}

//   @Test
//   @DisplayName("post /total invalid order item not on menu")
// 	public void postTotalInvalidNotOnMenu() throws Exception {
      
//       String mockJson = "{\"burger\":1}";
      
//       mockMvc.perform(post("/total") 
//                     .contentType(MediaType.APPLICATION_JSON)
//                     .content(mockJson))
//         .andExpect(status().is(422))
//         .andExpect(status().reason(containsString("No such item or counts not all whole numbers")))
//         .andExpect(jsonPath("$.body").doesNotExist());
//   }

//    @Test
//   @DisplayName("post /total invalid order negative quantity")
// 	public void postTotalInvalidNegativeQuantity() throws Exception {
      
//       String mockJson = "{\"veggie\":-1}";

//       mockMvc.perform(post("/total") 
//                     .contentType(MediaType.APPLICATION_JSON)
//                     .content(mockJson))
//         .andExpect(status().is(422))
//         .andExpect(status().reason(containsString("No such item or counts not all whole numbers")))
//         .andExpect(jsonPath("$.body").doesNotExist());
//   }
}