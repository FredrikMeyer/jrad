(define sqrt (lambda (x)
               (sqrt-iter 1.3 x)))

(define sqrt-iter (lambda (guess x)
                    (if (good-enough? guess x)
                      guess
                      (sqrt-iter (improve guess x) x))))

(define good-enough? (lambda (guess x)
                       (< (abs (- (square guess) x)) 0.01)))

(define improve (lambda (guess x)
                  (average guess (/ x guess))))

(define average (lambda (a b)
                  (* 0.5 (+ a b))))

(define square (lambda (x) (* x x)))

(sqrt 2)