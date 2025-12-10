package com.example.starwarsplanets.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponsePlanetDTO {

  private List<ResponsePlanetDTO> content;
  private long totalElements;
  private int totalPages;
  private int size;
  private int number;
  private boolean first;
  private boolean last;

  public PagedResponsePlanetDTO() {}

  public PagedResponsePlanetDTO(List<ResponsePlanetDTO> content, long totalElements, int totalPages,
      int size, int number, boolean first, boolean last) {
    this.content = content;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.size = size;
    this.number = number;
    this.first = first;
    this.last = last;
  }

  public List<ResponsePlanetDTO> getContent() {
    return content;
  }

  public void setContent(List<ResponsePlanetDTO> content) {
    this.content = content;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(long totalElements) {
    this.totalElements = totalElements;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public boolean isFirst() {
    return first;
  }

  public void setFirst(boolean first) {
    this.first = first;
  }

  public boolean isLast() {
    return last;
  }

  public void setLast(boolean last) {
    this.last = last;
  }
}
