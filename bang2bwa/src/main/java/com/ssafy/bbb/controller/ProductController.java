package com.ssafy.bbb.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.model.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;

}
