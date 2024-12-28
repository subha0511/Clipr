package com.subha.uri.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"}),})
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @JsonManagedReference // Forward reference for JSON serialization
  @ToString.Exclude     // Prevent Lombok recursion
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  private List<Url> urls;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
    return true;
  }

  @Override
  public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
    return true;
  }
}
