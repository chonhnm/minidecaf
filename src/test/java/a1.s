	.file	"q.c"
	.option nopic
	.attribute arch, "rv32i2p0_m2p0"
	.attribute unaligned_access, 0
	.attribute stack_align, 16
	.text
	.section	.text.startup,"ax",@progbits
	.align	2
	.globl	main
	.type	main, @function
main:
	sgt	a0,a0,a1
	ret
	.size	main, .-main
	.ident	"GCC: (GNU) 10.2.0"
