Feature: upload and edit new products
  Scenario: Can upload new product
    Given url 'http://localhost:8001/prettyparadise/products'
    And multipart file image = {read: 'image/gray_and_glitter.png', contentType: 'image/png'}
    And multipart field productDetails = read('json/name-and-price-request.json')
    When method post
    Then status 201

