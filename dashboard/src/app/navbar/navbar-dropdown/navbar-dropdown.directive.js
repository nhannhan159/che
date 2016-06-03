/*
 * Copyright (c) 2015-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';

/**
 * @ngdoc directive
 * @name dashboard.directive:NavbarDropdown
 * @restrict E
 * @element
 *
 * @description
 *  `<navbar-dropdown></navbar-dropdown>` defines a dropdown menu in navbar
 *
 * @param {array}    navbar-dropdown-items  the list of menu items
 * @param {boolean=} navbar-dropdown-above  the optional flag to situate menu above the button
 * @param {boolean=} navbar-dropdown-right-click  the optional flag to activate menu by right mouse click
 *
 * @usage
 *    <navbar-dropdown navbar-dropdown-items="ctrl.itemsList">
 *      <md-button>Show menu</md-button>
 *    </navbar-dropdown>
 *
 * @author Oleksii Kurinnyi
 */
export class NavbarDropdown {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor($timeout) {
    this.$timeout = $timeout;

    this.restrict = 'E';
    this.bindToController = true;
    this.templateUrl = 'app/navbar/navbar-dropdown/navbar-dropdown.html';
    this.controller = 'NavbarDropdownCtrl';
    this.controllerAs = 'navbarDropdownCtrl';

    this.transclude = true;
    this.replace = true;

    // scope values
    this.scope = {
      dropdownItems: '=navbarDropdownItems',
      moveDropdownAbove: '@?navbarDropdownAbove',
      rightClick: '@?navbarDropdownRightClick'
    };
  }

  link($scope, $element, $attrs, ctrl) {
    // set position of dropdown menu
    let elemHeight = $element.height(),
      dropdownOffset = elemHeight - 1,
      $dropdownList = $element.find('.navbar-dropdown-elements');
    if ($dropdownList.length) {
      if (ctrl.moveupDropdown) {
        $dropdownList.css('bottom', dropdownOffset + 'px');
      } else {
        $dropdownList.css('top', dropdownOffset + 'px');
      }
    }

    // handle click on backdrop
    let backdropEl = $element.find('.navbar-dropdown-backdrop');
    backdropEl.bind('click', (event) => {
      event.preventDefault();
      event.stopPropagation();

      $scope.$apply(() => {
        ctrl.closeDropdown();
      });
    });
    backdropEl.bind('contextmenu', (event) => {
      event.preventDefault();
      event.stopPropagation();

      $scope.$apply(() => {
        ctrl.closeDropdown();
      });
    });

    // handle click on button
    let buttonEl = $element.find('.navbar-dropdown-button')
    if (ctrl.rightClick) {
      buttonEl.bind('contextmenu', (event) => {
        event.preventDefault();
        event.stopPropagation();

        ctrl.toggleDropdown();
      })
    } else {
      buttonEl.bind('click', (event) => {
        ctrl.toggleDropdown();
      })
    }
  }
}
